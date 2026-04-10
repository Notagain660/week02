package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.Role;
import org.example.backend.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;

@TableName("user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class User implements Serializable {
    //builder：生成建造者模式的内部类，用于链式构造对象，不生成getter&setter
    //两个生成构造器
    //Lombok 的组合注解，包含：
    //@Getter（所有字段）@Setter（所有非 final 字段），@ToString，@EqualsAndHashCode
    //@RequiredArgsConstructor（如果有无参构造，则不影响）
    //Serializable是Java内置的标记接口(没有方法),表示对象可以被序列化(转换成字节流)
    //以便存储到文件、通过网络传输或放入缓存(如Redis、内存Session)
    //HttpSession：Tomcat 等容器在会话持久化（如重启后恢复）、集群会话复制时，会将 Session 中的对象序列化后存储到磁盘或发送给其他服务器。
    //如果不实现 Serializable，会报 NotSerializableException。
    //Redis：Redis 是键值数据库，存储 Java 对象时，Spring Data Redis 默认使用 JDK 序列化（或 JSON）。对象需要可序列化才能存进去。
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "userid", type = IdType.AUTO)//Idtype使userId自增而不是雪花算法生成随机数字（mybatisplus默认）
    @JsonSerialize(using = ToStringSerializer.class) //防止前端 JS 精度丢失
    private long userId;

    private Role role;
    @Size(min = 8, max = 43)
    private String password;

    @TableField("username")
    private String userName;//对应数据表的哪一列
    @TableField("useremail")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String userEmail;
    @TableField("userphone")
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String userPhone;
    @TableField("userpostnum")
    private int userPostNum;
    @TableField("useravatar")
    private String userAvatar;
    @TableField("userstatus")
    private UserStatus status;
}
