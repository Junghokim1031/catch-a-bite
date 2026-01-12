package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MENU_OPTION_GROUP")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"menu", "options"})
public class MenuOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_OPTION_GROUP_ID")
    private Long menuOptionGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID", nullable = false)
    private Menu menu;

    @Column(name = "MENU_OPTION_GROUP_NAME", nullable = false, length = 100)
    private String menuOptionGroupName;

    @Column(name = "MENU_OPTION_GROUP_REQUIRED", length = 1)
    private String menuOptionGroupRequired;

    @OneToMany(mappedBy = "menuOptionGroup", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuOption> options = new ArrayList<>();
}
