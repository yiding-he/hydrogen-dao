drop table if exists t_school_district;
create table t_school_district(
  id      int         primary key auto_increment,
  name    varchar(40) not null comment '学区名称',
  city_id int         not null comment '所属城市'
) comment '学区信息表';

drop table if exists t_school_info;
create table t_school_info(
  id                  int           primary key auto_increment,
  name                varchar(40)   not null comment '学校名称',
  school_district_id  int                    comment '所属学区ID'
) comment '学校信息表';

drop table if exists t_school_class_info;
create table t_school_class_info(
  id              int           primary key auto_increment,
  school_id       int           not null comment '所属学校ID',
  name            varchar(40)   not null comment '班级名称',
  admission_year  int           not null comment '入学年份',
  admission_grade int           not null comment '入学年级',
  unique key idx_name(admission_year,admission_grade,name)
) comment '学校班级信息表';

drop table if exists t_school_student_info;
create table t_school_student_info(
  id int primary key auto_increment,
  school_id       int           not null comment '所属学校ID',
  school_class_id int           not null comment '所属班级ID',
  student_name    varchar(40)   not null comment '学生姓名，班级内唯一',
  unique key idx_name(school_class_id, student_name)
) comment '学校学生信息表';


