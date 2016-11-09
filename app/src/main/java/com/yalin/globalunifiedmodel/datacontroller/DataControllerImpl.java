package com.yalin.globalunifiedmodel.datacontroller;


import com.yalin.datacontroller.MaybeConsumer;
import com.yalin.datacontroller.Success;
import com.yalin.globalunifiedmodel.metadata.MetaDataManager;
import com.yalin.globalunifiedmodel.metadata.UserModel;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * 作者：YaLin
 * 日期：2016/10/26.
 */

public class DataControllerImpl implements DataController {
    private final Executor mUiThread;
    private final Executor mMetaDataThread;
    private MetaDataManager mMetaDataManager;

    public DataControllerImpl(Executor uiThread, Executor metaDataThread, MetaDataManager metaDataManager) {
        mUiThread = uiThread;
        mMetaDataThread = metaDataThread;
        mMetaDataManager = metaDataManager;
    }

    private <T> void background(Executor dataThread, final MaybeConsumer<T> onSuccess,
                                final Callable<T> job) {
        dataThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final T result = job.call();
                    mUiThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            onSuccess.success(result);
                        }
                    });
                } catch (final Exception e) {
                    mUiThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            onSuccess.fail(e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void login(final UserModel user, MaybeConsumer<UserModel> onSuccess) {
        background(mMetaDataThread, onSuccess, new Callable<UserModel>() {
            @Override
            public UserModel call() throws Exception {
                long id = mMetaDataManager.login(user);
                return user.copyUserWithId(id);
            }
        });
    }

    @Override
    public void update(final UserModel user, MaybeConsumer<Success> onSuccess) {
        background(mMetaDataThread, onSuccess, new Callable<Success>() {
            @Override
            public Success call() throws Exception {
                mMetaDataManager.update(user);
                return Success.SUCCESS;
            }
        });
    }

    @Override
    public void exit(MaybeConsumer<Success> onSuccess) {
        background(mMetaDataThread, onSuccess, new Callable<Success>() {
            @Override
            public Success call() throws Exception {
                mMetaDataManager.exit();
                return Success.SUCCESS;
            }
        });
    }

    @Override
    public void withLoginUser(MaybeConsumer<UserModel> onSuccess) {
        background(mMetaDataThread, onSuccess, new Callable<UserModel>() {
            @Override
            public UserModel call() throws Exception {
                return mMetaDataManager.getLoginUser();
            }
        });
    }

    @Override
    public UserModel getLoginUser() {
        return mMetaDataManager.getLoginUser();
    }
}
