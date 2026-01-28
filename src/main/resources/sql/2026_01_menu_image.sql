-- menu 대표 이미지 컬럼 추가
alter table menu add column menu_thumbnail_url varchar(500) null;

-- menu_image 테이블 생성
create table if not exists menu_image (
    menu_image_id bigint not null auto_increment,
    menu_id bigint not null,
    menu_image_url varchar(500) not null,
    menu_image_is_main bit not null,
    primary key (menu_image_id),
    constraint fk_menu_image_menu foreign key (menu_id) references menu (menu_id)
);

create index idx_menu_image_menu_id on menu_image (menu_id);
