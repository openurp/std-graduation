[#ftl]
[@b.head/]
  [@b.grid items=degree2ndApplies var="degree2ndApply"]
    [@b.gridbar]
      bar.addItem("查看", action.info());
      bar.addItem("${b.text("action.export")}",action.exportData("std.code:学号,std.name:姓名,std.person.code:证件号码,std.state.grade:年级,std.state.department.name:所在学院,std.state.major.name:专业,std.state.squad.name:班级,gpa:基础课绩点,gradeDetail:成绩明细,updatedAt:申请时间",null,'fileName=二学位申请信息'));
      bar.addItem("批量重新绩点",action.multi("recalc","确定计算更新已选记录的绩点?"));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.code" width="13%"/]
      [@b.col title="姓名" property="std.name" width="8%"]
        [@b.a href="!info?id="+degree2ndApply.id title="查看"]${degree2ndApply.std.name}[/@]
      [/@]
      [@b.col title="学院" property="std.state.department.name"  width="10%"]
       ${(degree2ndApply.std.state.department.shortName)!(degree2ndApply.std.state.department.name)!}
      [/@]
      [@b.col title="专业" property="std.state.major.name" width="19%"/]
      [@b.col title="班级" property="std.state.squad.name" width="24%"/]
      [@b.col title="基础课绩点" property="gpa" width="10%"]
        ${degree2ndApply.gpa?string("#.0000")}
      [/@]
      [@b.col title="申请日期" width="10%" property="updatedAt"]
        ${degree2ndApply.updatedAt?string("yyyy-MM-dd")}
      [/@]
    [/@]
  [/@]
[@b.foot/]
