package dev.agasen.ecom.util.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
class GlobalControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

//   @ResponseStatus(NOT_FOUND)
//   @ExceptionHandler(NotFoundException.class)
//   public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
//     ServerHttpRequest request, NotFoundException ex) {

//     return createHttpErrorInfo(NOT_FOUND, request, ex);
//   }

//   @ResponseStatus(UNPROCESSABLE_ENTITY)
//   @ExceptionHandler(NoCorrectAnswerException.class)
//   public @ResponseBody HttpErrorInfo handleInvalidInputException(
//     ServerHttpRequest request, NoCorrectAnswerException ex) {

//     return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
//   }

//   private HttpErrorInfo createHttpErrorInfo(
//     HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {

//     final String path = request.getPath().pathWithinApplication().value();
//     final String message = ex.getMessage();

//     LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
//     return new HttpErrorInfo(httpStatus, path, message);
//   }
}
