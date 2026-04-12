package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.OtherInfo;
import org.example.backend.entity.Relation;
import org.example.backend.entity.User;
import org.example.backend.enums.OurStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.enums.UserStatus;
import org.example.backend.mapper.RelationMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class RelationService {
    private final UserMapper userMapper;
    private final RelationMapper relationMapper;

    public OtherInfo getOtherProfile(long userId){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        return new OtherInfo(user.getUserAvatar(),user.getUserName()
                ,user.getStatus(),user.getUserPostNum());
    }

    public List<Relation> getRelations(OurStatus ourStatus){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        LambdaQueryWrapper<Relation> wrapper = new LambdaQueryWrapper<>();
        if(ourStatus.getValue() == 1){
        wrapper.eq(Relation::getItsId, userMe.getUserId())
               .eq(Relation::getOurStatus, ourStatus);
        }else if(ourStatus.getValue() == 2){
            wrapper.and(w -> w.eq(Relation::getMyId, userMe.getUserId())
                            .eq(Relation::getOurStatus, ourStatus))
                    .or(w -> w.eq(Relation::getItsId, userMe.getUserId())
                            .eq(Relation::getOurStatus, ourStatus));
        }
        return relationMapper.selectList(wrapper);//好吧还可以改进为不需要返回自己的id（VO）
    }

    public boolean request(Long userId) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        User user = userMapper.selectById(userId);

        if (user.getStatus() == UserStatus.BLOCKED || userMe.getStatus() == UserStatus.BLOCKED) {
            throw new BusiException(StatusCode.USERBLOCKED, "禁止添加对方为好友");
        }
        if (Objects.equals(userMe.getUserId(), user.getUserId())) {
            throw new BusiException(StatusCode.NOSELFRELA);
        }
        //检查是否为好友或者已申请过
        LambdaQueryWrapper<Relation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Relation::getMyId, userMe.getUserId())
                        .eq(Relation::getItsId, userId))
                .or(w -> w.eq(Relation::getMyId, userId)
                        .eq(Relation::getItsId, userMe.getUserId()));
        Relation relation = relationMapper.selectOne(wrapper);
        if (relation != null) {
            if (relation.getOurStatus().getValue() == 2) {
                throw new BusiException(StatusCode.INVALID, "你已添加对方为好友");
            } else if (relation.getOurStatus().getValue() == 1) {
                throw new BusiException(StatusCode.INVALID, "你已经发送好友申请");
            } else if (relation.getOurStatus().getValue() == 0) {
                relation.setOurStatus(OurStatus.REQUESTING);
                return relationMapper.updateById(relation) == 1;
            }
        }

        relation = new Relation();
        relation.setItsId(userId);
        relation.setMyId(userMe.getUserId());
        relation.setOurStatus(OurStatus.REQUESTING);
        return relationMapper.insert(relation) == 1;
    }

    public boolean dealRelation(Long userId, OurStatus ourStatus, int act) {
        //act区分拒绝、同意还是删除，controller层按照act码不同分开三个方法（0，1，2）

        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        User user = userMapper.selectById(userId);

        if(act != 0 && act != 1 && act != 2){
            throw new BusiException(StatusCode.INVALID);
        }

        if (user.getStatus() == UserStatus.BLOCKED  || userMe.getStatus() == UserStatus.BLOCKED) {
            throw new BusiException(StatusCode.INVALID, "封禁状态禁止操作");
        }
        //找记录
        LambdaQueryWrapper<Relation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Relation::getMyId, userMe.getUserId())
                        .eq(Relation::getItsId, userId))
                .or(w -> w.eq(Relation::getMyId, userId)
                        .eq(Relation::getItsId, userMe.getUserId()))
                .eq(Relation::getOurStatus, ourStatus);
        Relation relation = relationMapper.selectOne(wrapper);

        if (relation == null) {//没有进行过任何操作的情况，必须有了好友关系/添加动作才能下一步
            throw new BusiException(StatusCode.INVALID);
        } else {//有操作的情况
            if (relation.getOurStatus().getValue() == 1) {
                //REQUESTING状态，用户是被请求方时可以同意/拒绝
                if(!Objects.equals(relation.getItsId(), userMe.getUserId())){//如果不是被请求方
                    throw new BusiException(StatusCode.INVALID);
                } else {
                    if(act == 1){//同意
                        relation.setOurStatus(OurStatus.kNITOK);
                        relation.setOkTime(LocalDateTime.now());
                    } else if(act == 0){//拒绝
                        relation.setOurStatus(OurStatus.NOKNIT);
                    }
                }
            } else if(relation.getOurStatus().getValue() == 2 &&
                     (Objects.equals(relation.getItsId(), userMe.getUserId()) ||
                             Objects.equals(relation.getMyId(), userMe.getUserId()))){//删除好友，用户是关系一方
                relation.setOurStatus(OurStatus.NOKNIT);
                relation.setOkTime(null);
            } else {
                throw new BusiException(StatusCode.INVALID);//不是这两个状态不管
            }
        }
        LambdaUpdateWrapper<Relation> combinedWrapper = new LambdaUpdateWrapper<>();
        combinedWrapper.and(cw -> cw.eq(Relation::getMyId, userMe.getUserId())
                                .eq(Relation::getItsId, userId))
                       .or(cw -> cw.eq(Relation::getMyId, userId)
                                .eq(Relation::getItsId, userMe.getUserId()));
        //按照联合主键更新
        return  relationMapper.update(relation, combinedWrapper) == 1;
    }


}
