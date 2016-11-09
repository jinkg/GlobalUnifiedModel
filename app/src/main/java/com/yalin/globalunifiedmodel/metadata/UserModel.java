package com.yalin.globalunifiedmodel.metadata;

/**
 * 作者：YaLin
 * 日期：2016/10/26.
 */

public class UserModel {
    public final long id;
    public String name;
    public int age;

    public UserModel() {
        id = -1;
    }

    public UserModel(long id) {
        this.id = id;
    }

    public UserModel copyUserWithId(long id) {
        UserModel newUser = new UserModel(id);
        newUser.name = name;
        newUser.age = age;
        return newUser;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
