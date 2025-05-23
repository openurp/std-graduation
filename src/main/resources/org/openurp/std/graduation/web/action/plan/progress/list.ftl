[#ftl/]
[@b.head/]
[@b.grid items=results! var="result" id="result_list_table"]
  [@b.gridbar]
    bar.addItem('未过课程', action.multi("failCourses"));
    bar.addItem("${b.text("action.export")}",
        action.exportData("std.code:学号,std.name:姓名,std.state.grade:年级,"+
        "std.state.department.name:院系,std.state.major.name:专业,requiredCredits:应修,owedCredits:缺分,passed:是否完成,"+
        "result:缺分明细,result3:预计缺分明细",null,'fileName=计划完成情况'));
  [/@]
  [@b.row]
  [@b.boxcol/]
  [@b.col title="学号" property="std.code" width="10%"/]
  [@b.col title="姓名" property="std.name" width="7%"]
    <div class="text-ellipsis">${(result.std.name)!}</div>
  [/@]
  [@b.col title="年级" property="std.state.grade" width="5%" /]
  [@b.col title="院系" property="std.state.department.name" width="6%"]
    ${result.std.state.department.shortName!result.std.state.department.name}
  [/@]
  [@b.col title="专业" property="std.state.major.name" width="8%" ]
    <div class="text-ellipsis">${(result.std.state.major.name)!}</div>
  [/@]
  [@b.col title="应修" property="requiredCredits" width="6%"/]
  [@b.col title="缺分" property="owedCredits" width="5%"] [#if result.owedCredits>0]${result.owedCredits}[/#if] [/@]
  [@b.col title="预计缺" property="owedCredits3" width="5%"] [#if result.owedCredits3>0]${result.owedCredits3}[/#if] [/@]
  [@b.col title="结果" property="passed" width="7%"]
    [@b.a href="!info?id=" + result.id title="查看审核结果" target="_blank"]${result.passed?string("完成","<font color='red'>未完成</font>")}[/@]
  [/@]
  [@b.col title="未完成学分"]
    <div class="text-ellipsis">
    [#list result.groupResults?sort_by("indexno") as r]
      [#if (!r.parent?? || !(r.parent.parent??)) && !r.passed && !r.predicted && r.owedCredits>0]${r.courseType.name}${r.owedCredits}分&nbsp;[/#if]
    [/#list]
    </div>
  [/@]
  [/@]
[/@]
[@b.foot/]
