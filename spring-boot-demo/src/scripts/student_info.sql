drop table if exists t_school_district;
create table t_school_district(
  id      int         primary key auto_increment,
  name    varchar(40) not null comment 'ѧ������',
  city_id int         not null comment '��������'
) comment 'ѧ����Ϣ��';

drop table if exists t_school_info;
create table t_school_info(
  id                  int           primary key auto_increment,
  name                varchar(40)   not null comment 'ѧУ����',
  school_district_id  int                    comment '����ѧ��ID'
) comment 'ѧУ��Ϣ��';

drop table if exists t_school_class_info;
create table t_school_class_info(
  id              int           primary key auto_increment,
  school_id       int           not null comment '����ѧУID',
  name            varchar(40)   not null comment '�༶����',
  admission_year  int           not null comment '��ѧ���',
  admission_grade int           not null comment '��ѧ�꼶',
  unique key idx_name(admission_year,admission_grade,name)
) comment 'ѧУ�༶��Ϣ��';

drop table if exists t_school_student_info;
create table t_school_student_info(
  id int primary key auto_increment,
  school_id       int           not null comment '����ѧУID',
  school_class_id int           not null comment '�����༶ID',
  student_name    varchar(40)   not null comment 'ѧ���������༶��Ψһ',
  unique key idx_name(school_class_id, student_name)
) comment 'ѧУѧ����Ϣ��';


