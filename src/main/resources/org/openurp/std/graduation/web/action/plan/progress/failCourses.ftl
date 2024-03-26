[#ftl]
[@b.head/]
[@b.toolbar title="未完成课程"]
  bar.addBack();
[/@]
  [@b.grid items=courseResults var="result"]
    [@b.row]
      [@b.col title="学号" property="groupResult.planResult.std.code" width="10%"]
          ${result.groupResult.planResult.std.code}
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
      [@b.col title="课程名称" property="course.name" width="17%"]<span title="${result.course.code} ${result.course.defaultCredits}分">${result.course.name}</span>[/@]
      [@b.col title="课程类别" property="groupResult.courseType.name" width="15%"/]
      [@b.col title="学分" width="5%"]
        ${result.course.getCredits(result.groupResult.planResult.std.level)}
      [/@]
      [@b.col title="成绩" property="scores" width="6%"/]
      [@b.col title="备注" property="remark" style="text-align:left;padding-left:7px;"]
        <div style="font-size:0.8em" class="text-ellipsis">${(result.remark!"")?replace("\n","<br>")}</div>
      [/@]
    [/@]
  [/@]
[@b.foot/]
