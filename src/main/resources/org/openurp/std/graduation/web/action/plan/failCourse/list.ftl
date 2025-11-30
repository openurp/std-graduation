[#ftl]
[@b.head/]
  [@b.grid items=results var="result"]
    [@b.gridbar]
      bar.addItem("${b.text("action.export")}", action.exportData("groupResult.planResult.std.code:学号,groupResult.planResult.std.name:姓名,groupResult.planResult.std.state.grade.name:年级,groupResult.planResult.std.level.name:培养层次,groupResult.planResult.std.state.department.name:院系,groupResult.planResult.std.state.major.name:专业,groupResult.planResult.std.state.squad.name:班级,course.code:课程代码,course.name:课程名称,groupResult.courseType.name:课程类别,scores:成绩,remark:备注"));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="groupResult.planResult.std.code" width="10%"]
        [@b.a target="_blank" href="/plan/plan-audit-search!instantAudit.action?stdId="+result.groupResult.planResult.std.id]
          ${result.groupResult.planResult.std.code}
        [/@]
      [/@]
      [@b.col title="姓名" property="groupResult.planResult.std.name"  width="6%"]
        <div class="text-ellipsis">${(result.groupResult.planResult.std.name)!}</div>
      [/@]
      [@b.col title="年级" property="groupResult.planResult.std.state.grade.code"  width="6%"/]
      [@b.col title="层次" property="groupResult.planResult.std.level.name"  width="6%"/]
      [@b.col title="院系" property="groupResult.planResult.std.state.department.name"  width="6%"]
        ${result.groupResult.planResult.std.state.department.shortName!result.groupResult.planResult.std.state.department.name}
      [/@]
      [@b.col title="专业" property="groupResult.planResult.std.state.major.name"  width="8%"]
        <div class="text-ellipsis">${(result.groupResult.planResult.std.state.major.name)!}</div>
      [/@]
      [@b.col title="班级" property="groupResult.planResult.std.state.squad.name"  width="8%"]
        <div class="text-ellipsis">${(result.groupResult.planResult.std.state.squad.name)!}</div>
      [/@]
      [@b.col title="课程名称" property="course.name" width="17%"]
      <span title="${result.course.code} ${result.course.defaultCredits}分">${result.course.code} ${result.course.name}</span>[/@]
      [@b.col title="课程类别" property="groupResult.courseType.name" width="15%"/]
      [@b.col title="成绩" property="scores" width="6%"/]
      [@b.col title="备注" property="remark" style="text-align:left;padding-left:7px;"]
        <div style="font-size:0.8em" class="text-ellipsis" title="${result.remark!}">${(result.remark!"")?replace("\n","<br>")}</div>
      [/@]
    [/@]
  [/@]
[@b.foot/]
