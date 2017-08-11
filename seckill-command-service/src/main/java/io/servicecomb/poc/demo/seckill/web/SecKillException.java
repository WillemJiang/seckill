package io.servicecomb.poc.demo.seckill.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.PRECONDITION_FAILED,reason = "bad request data")
public class SecKillException extends RuntimeException {

}
