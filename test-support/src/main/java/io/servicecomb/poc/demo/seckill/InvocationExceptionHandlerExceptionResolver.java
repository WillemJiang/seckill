/*
 *   Copyright 2017 Huawei Technologies Co., Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.servicecomb.poc.demo.seckill;

import java.lang.reflect.Method;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import io.servicecomb.swagger.invocation.exception.InvocationException;

public class InvocationExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
  @Override
  protected ServletInvocableHandlerMethod getExceptionHandlerMethod(final HandlerMethod handlerMethod,
      final Exception exception) {
    if (((InvocationException) exception).getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
      Method method = new ExceptionHandlerMethodResolver(BadRequestExceptionAdvice.class).resolveMethod(exception);
      return new ServletInvocableHandlerMethod(new BadRequestExceptionAdvice(), method);
    } else if (((InvocationException) exception).getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
      Method method = new ExceptionHandlerMethodResolver(TooManyRequestsExceptionAdvice.class).resolveMethod(exception);
      return new ServletInvocableHandlerMethod(new TooManyRequestsExceptionAdvice(), method);
    }
    return super.getExceptionHandlerMethod(handlerMethod, exception);
  }
}
