
drop table if exists t_exam_project;
create table t_exam_project(
  id 	int 			primary key auto_increment,
  name 	varchar(100)	comment '考试名称'
) comment '考试项目表';

drop table if exists t_exam_district;
create table t_exam_district(
  id  int       primary key auto_increment,
) comment '参与考试学区表';

drop table if exists t_exam_school;
create table t_exam_school(
  id  int       primary key auto_increment,
) comment '参与考试学校表';

drop table if exists t_exam_class;
create table t_exam_class(
  id  int       primary key auto_increment,
) comment '参与考试班级表';

drop table if exists t_exam_student;
create table t_exam_student(
  id  int       primary key auto_increment,
) comment '参与考试学生表';
