package org.example.backend.tokener;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtHelper jwtHelper;

    @Override
    //重写handlerinterceptor的prehandle方法，Spring MVC的拦截器HandlerInterceptor
    //该方法要求三个参数，最后一个是Object handler,通常是handlerMethod实例，它封装了
    //目标Controller类、目标方法（比如 UserController.login()）、方法参数等信息
    //没有用到 handler但是要写
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception{

        final Pattern BEARER_PATTERN = Pattern.compile("^Bearer ([A-Za-z0-9\\-._~+/]+=*)$");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing Authorization header\"}");
            return false;
        }

        Matcher matcher = BEARER_PATTERN.matcher(authHeader);
        if (!matcher.matches()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid token format. Expected 'Bearer <token>'\"}");
            return false;
        }

        String token = matcher.group(1);

        if (!jwtHelper.validateTokenWithTime(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("Invalid token");
            return false;
        }

        ThreadContext.UserInfo userInfo = jwtHelper.getUserInfoFromToken(token);
        if (userInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("ThreadLocal set failure");
            return false;
        }
        ThreadContext.setCurrentUser(userInfo);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex)  {
        // 清除 ThreadLocal，避免内存泄漏
        ThreadContext.clear();
    }

}
