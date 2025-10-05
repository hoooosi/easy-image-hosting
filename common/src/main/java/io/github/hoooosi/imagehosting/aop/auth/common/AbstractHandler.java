package io.github.hoooosi.imagehosting.aop.auth.common;

/**
 * Abstract permission processor, used to provide authentication services for
 * different types of IDs
 */
public abstract class AbstractHandler implements Handler {

    protected AbstractHandler nextHandler;

    public AbstractHandler setNextHandler(AbstractHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    protected Long invokeNext(Long id) {
        if (nextHandler != null) {
            return nextHandler.handlePermission(id);
        }
        return null;
    }
}