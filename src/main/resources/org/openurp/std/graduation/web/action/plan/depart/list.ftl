[#ftl/]
[@b.head/]

[#assign resultType = "result"/]
[#assign predictedParam=(Parameters['predicted']!"--")/]
[#if predictedParam == "passed" || predictedParam == "unpassed"]
  [#assign resultType = "result2"/]
[#elseif predictedParam =="takingPassed" || predictedParam =="takingFailed"]
  [#assign resultType = "result3"/]
[/#if]
[#assign resultTypeNames={'result':'未完成学分','result2':'预审未完成学分','result3':'在读通过后，预计未完成'}/]

[@b.grid items=results! var="result" id="result_list_table"]
  [@b.gridbar]
    bar.addItem('审核', action.multi("audit"));
    bar.addItem('未过课程', action.multi("failCourses"));
    bar.addItem("${b.text("action.export")}",action.exportData("std.code:学号,std.name:姓名,std.gender.name:性别,std.level.name:培养层次,"+
                "std.stdType.name:学生类别,std.state.department.name:院系,std.state.major.name:专业,std.state.direction.name:方向,std.state.status.name:学籍状态,"+
                "passed:是否通过,predicted:预审是否通过,requiredCredits:要求学分,passedCredits:完成学分,owedCredits:未完成学分," +
                "owedCredits2:预审未完成学分,owedCredits3:在读课程通过后，还需学分,result:未完成明细,result3:在读课程通过后预计未完成明细",
                null,'fileName=计划完成审核结果'));
  [/@]
  [@b.row]
  [@b.boxcol/]
  [@b.col title="学号" property="std.code" width="10%"/]
  [@b.col title="姓名" property="std.name" width="7%"]
    <div class="text-ellipsis" title="${result.std.name}">${(result.std.name)!}</div>
  [/@]
  [@b.col title="年级" property="std.state.grade" width="5%" /]
  [@b.col title="培养层次" property="std.level.name" width="6%" /]
  [@b.col title="院系" property="std.state.department.name" width="6%"]
    ${result.std.state.department.shortName!result.std.state.department.name}
  [/@]
  [@b.col title="专业" property="std.state.major.name" width="8%" ]
    <div class="text-ellipsis">${(result.std.state.major.name)!}</div>
  [/@]
  [@b.col title="应修" property="requiredCredits" width="5%"/]
  [@b.col title="缺分" property="owedCredits" width="5%"] [#if result.owedCredits>0]${result.owedCredits}[/#if] [/@]
  [@b.col title="预计缺" property="owedCredits3" width="5%"]${result.owedCredits3}[/@]
  [@b.col title="结果" property="passed" width="10%"]
    [@b.a href="!info?id=" + result.id title="查看审核结果" target="_blank"]${result.passed?string("完成","<font color='red'>未完成</font>")}[/@]
    [#if !result.passed && result.predicted]<span class="text-muted">预计&#10004;</span>[/#if]
  [/@]
  [@b.col title=resultTypeNames[resultType]]
    <div class="text-ellipsis">
    [#if resultType=="result"]
      [#list result.failedGroups(1?int) as g]${(g.courseType.shortName)!g.name}${g.owedCredits}分[#sep]&nbsp;[/#list]
    [#elseif resultType="result2"]
      [#list result.failedGroups(2?int) as g]${(g.courseType.shortName)!g.name}${g.owedCredits2}分[#sep]&nbsp;[/#list]
    [#elseif resultType="result3"]
      [#list result.failedGroups(3?int) as g]${(g.courseType.shortName)!g.name}${g.owedCredits3}分[#sep]&nbsp;[/#list]
    [/#if]
    </div>

  [/@]
  [/@]
[/@]
<script>
  jQuery(function() {
    $('#result_list_table [data-toggle="tooltip"]').tooltip({'html':true})
  });
</script>
[@b.foot/]
