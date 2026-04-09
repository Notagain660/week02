package org.example.backend.utilities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.StatusCode;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@Data
public class MapperResult<T> implements Serializable {

    private int code;
    private String message;
    private T data;
    public MapperResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> MapperResult<T> success(String message, T data) {
        return new MapperResult<>(200, message, data);
    }
    public static <T> MapperResult<T> success(StatusCode code, T data) {
        return new MapperResult<>(code.getCode(), code.getMessage(), data);
    }

    public static <T> MapperResult<T> error(StatusCode code){
        return new MapperResult<>(code.getCode(), code.getMessage(), null);
    }
    public static <T> MapperResult<T> error(StatusCode code, String message){
        return new MapperResult<>(code.getCode(), message, null);
    }
}

