package org.forgerock.openam.mobile.commons;

import org.apache.http.HttpResponse;

public class ResponseUnwrapperFactory implements IResponseUnwrapperFactory {

    @Override
    public IResponseUnwrapper createUnwrapper(HttpResponse res, ActionType succeed, ActionType fail) {
        return new ResponseUnwrapper(res, succeed, fail);
    }
}
