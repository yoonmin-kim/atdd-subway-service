package nextstep.subway.auth.domain;

import nextstep.subway.auth.domain.discount.DiscountPolicy;

public class LoginMember {
    private Long id;
    private String email;
    private Age age;

    public LoginMember() {
    }

    public LoginMember(Long id, String email, Integer age) {
        this.id = id;
        this.email = email;
        this.age = new Age(age);
    }

    public DiscountPolicy createDiscountPolicy() {
        return age.createDiscountPolicy();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age.getAge();
    }
}
