package org.forgerock.openam.mobile.commons;

import org.apache.http.HttpResponse;

public interface IResponseUnwrapperFactory {

    public IResponseUnwrapper createUnwrapper(HttpResponse res, ActionType succeed, ActionType fail);

}
