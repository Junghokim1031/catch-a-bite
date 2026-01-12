package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MENU_OPTION")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "menuOptionGroup")
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_OPTION_ID")
    private Long menuOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_OPTION_GROUP_ID", nullable = false)
    private MenuOptionGroup menuOptionGroup;

    @Column(name = "MENU_OPTION_NAME", nullable = false, length = 100)
    private String menuOptionName;

    @Column(name = "MENU_OPTION_PRICE")
    private Integer menuOptionPrice;
}
