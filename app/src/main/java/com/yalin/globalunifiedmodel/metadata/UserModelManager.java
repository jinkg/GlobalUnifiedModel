package com.yalin.globalunifiedmodel.metadata;

import android.content.Context;

import com.yalin.datacontroller.MaybeConsumer;
import com.yalin.datacontroller.Success;
import com.yalin.globalunifiedmodel.assertion.Assertions;
import com.yalin.globalunifiedmodel.datacontroller.AppSingleton;
import com.yalin.globalunifiedmodel.datacontroller.LoggingConsumer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * YaLin
 * 2016/11/9.
 */

public class UserModelManager {
    public interface UserModelUpdateListener {
        void onUserModelUpdate(UserModel oldUser, UserModel newUser);
    }

    private static final String TAG = "UserModelManager";

    private static UserModelManager sInstance;

    private Context mContext;

    private UserModel mCachedUser = null;

    private Set<UserModelUpdateListener> mListeners = new HashSet<>();

    private ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();

    public static UserModelManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserModelManager(context);
        }
        return sInstance;
    }

    private UserModelManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void registerUserModelUpdateListener(UserModelUpdateListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    public void unregisterUserModelUpdateListener(UserModelUpdateListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }

    public void login(UserModel userModel, final MaybeConsumer<UserModel> onSuccess) {
        AppSingleton.getInstance(mContext)
                .getDataController()
                .login(userModel, onSuccess);
    }

    public ThreadLocal<UserModel> getLoginUser() {
        try {
            mReadWriteLock.readLock().lock();
            if (mCachedUser == null) {
                mCachedUser = AppSingleton.getInstance(mContext)
                        .getDataController()
                        .getLoginUser();
            }
            ThreadLocal<UserModel> threadLocalUser = new ThreadLocal<>();
            threadLocalUser.set(mCachedUser);
            return threadLocalUser;
        } finally {
            mReadWriteLock.readLock().unlock();
        }
    }

    public void withLoginUser(final MaybeConsumer<UserModel> onSuccess) {
        Assertions.checkMainThread();
        try {
            mReadWriteLock.readLock().lock();
            if (mCachedUser != null) {
                onSuccess.success(mCachedUser);
            } else {
                AppSingleton.getInstance(mContext)
                        .getDataController()
                        .withLoginUser(new LoggingConsumer<UserModel>(TAG, "get login user") {
                            @Override
                            public void success(UserModel value) {
                                mCachedUser = value;
                                onSuccess.success(mCachedUser);
                            }
                        });
            }
        } finally {
            mReadWriteLock.readLock().unlock();
        }
    }

    public void updateLoginUser(UserModel newUserModel) {
        UserModel oldUser = mCachedUser;
        try {
            mReadWriteLock.writeLock().lock();
            if (mCachedUser == null) {
                return;
            }
            mCachedUser.name = newUserModel.name;
            mCachedUser.age = newUserModel.age;

            AppSingleton.getInstance(mContext)
                    .getDataController()
                    .update(mCachedUser, new LoggingConsumer<Success>(TAG, "update login user") {
                        @Override
                        public void success(Success value) {

                        }
                    });
        } finally {
            mReadWriteLock.writeLock().unlock();
        }
        notifyUserUpdate(oldUser, newUserModel);
    }

    // todo notify user update in main thread
    private void notifyUserUpdate(UserModel oldUser, UserModel newUser) {
//        ThreadLocal<UserModel> threadLocalOldUser = new ThreadLocal<>();
//        ThreadLocal<UserModel> threadLocalNewUser = new ThreadLocal<>();
//        threadLocalOldUser.set(oldUser);
//        threadLocalNewUser.set(newUser);
//        for()
    }
}
