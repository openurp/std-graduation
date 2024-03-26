[#ftl]
[@b.head/]
  [@b.grid items=applies var="apply"]
    [@b.gridbar]
      bar.addItem("查看", action.info());
      bar.addItem("${b.text("action.export")}",action.exportData("std.code:学号,std.name:姓名,std.person.code:证件号码,std.state.grade:年级,std.state.department.name:所在学院,std.state.major.name:专业,std.state.squad.name:班级,gpa:基础课绩点,gradeDetail:成绩明细,updatedAt:申请时间",null,'fileName=二学位申请信息'));
      bar.addItem("批量重新绩点",action.multi("recalc","确定计算更新已选记录的绩点?"));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.code" width="13%"/]
      [@b.col title="姓名" property="std.name" width="8%"]
        [@b.a href="!info?id="+apply.id title="查看"]${apply.std.name}[/@]
      [/@]
      [@b.col title="培养层次" property="std.level.name" width="8%"/]
      [@b.col title="年级" property="std.state.grade.name" width="8%"/]
      [@b.col title="学院" property="std.state.department.name"  width="10%"]
       ${(apply.std.state.department.shortName)!(apply.std.state.department.name)!}
      [/@]
      [@b.col title="专业/方向" property="std.state.major.name" ]
        ${(apply.std.state.major.name)!} ${(apply.std.state.direction.name)!}
      [/@]
      [@b.col title="基础课绩点" property="gpa" width="10%"]
        ${apply.gpa?string("#.0000")}
      [/@]
      [@b.col title="申请日期" width="10%" property="updatedAt"]
        ${apply.updatedAt?string("yyyy-MM-dd")}
      [/@]
    [/@]
  [/@]
[@b.foot/]
