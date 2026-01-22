package com.deliveryapp.catchabite.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_option_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MenuOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_option_group_id", nullable = false)
    private Long menuOptionGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "menu_option_group_name")
    private String menuOptionGroupName;

    @Column(name = "menu_option_group_required")
    private Boolean menuOptionGroupRequired;

    @OneToMany(mappedBy = "menuOptionGroup", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuOption> menuOptions = new ArrayList<>();

    public void changeInfo(String name, Boolean required) {
	this.menuOptionGroupName = name;
	this.menuOptionGroupRequired = required;
    }

}
