package com.yalin.globalunifiedmodel.metadata;

/**
 * 作者：YaLin
 * 日期：2016/10/26.
 */

public interface MetaDataManager {
    long login(UserModel user);

    void update(UserModel newUser);

    void exit();

    UserModel getLoginUser();

    void close();
}
