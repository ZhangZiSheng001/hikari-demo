/**
 *
 */
package cn.zzs.hikari.entity;

import java.util.Date;
import java.util.Objects;

/**
 * 用户实体类
 * @author: zzs
 * @date: 2019年11月03日 下午10:09:08
 */
public class User {

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户年龄
     */
    private Integer age;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 记录创建时间
     */
    private Date gmt_create;

    /**
     * 记录最近更新时间
     */
    private Date gmt_modified;

    /**
     * 电话号码
     */
    private String phone;

    public User() {
    }

    public User(String id, String name, Integer gender, Integer age, Date gmt_create, Date gmt_modified, String phone) {
        this.id = id;
        this.name = name;
        this.gender =gender;
        this.age = age;
        this.gmt_create = gmt_create;
        this.gmt_modified = gmt_modified;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }

    public Date getGmt_modified() {
        return gmt_modified;
    }

    public void setGmt_modified(Date gmt_modified) {
        this.gmt_modified = gmt_modified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(gender, user.gender) && Objects.equals(age, user.age) && Objects.equals(deleted, user.deleted) && Objects.equals(gmt_create, user.gmt_create) && Objects.equals(gmt_modified, user.gmt_modified) && Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, age, deleted, gmt_create, gmt_modified, phone);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", gender=").append(gender);
        sb.append(", age=").append(age);
        sb.append(", deleted=").append(deleted);
        sb.append(", gmt_create=").append(gmt_create);
        sb.append(", gmt_modified=").append(gmt_modified);
        sb.append(", phone='").append(phone).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
