package com.yalin.globalunifiedmodel.datacontroller;

import com.yalin.datacontroller.MaybeConsumer;
import com.yalin.datacontroller.Success;
import com.yalin.globalunifiedmodel.metadata.UserModel;

/**
 * 作者：YaLin
 * 日期：2016/10/26.
 */

public interface DataController {
    void login(UserModel user, MaybeConsumer<UserModel> onSuccess);

    void update(UserModel user, MaybeConsumer<Success> onSuccess);

    void exit(MaybeConsumer<Success> onSuccess);

    void withLoginUser(MaybeConsumer<UserModel> onSuccess);

    UserModel getLoginUser();
}
