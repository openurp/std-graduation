[@b.head/]
<div class="container">
  [#assign std=result.std/]
  [@b.toolbar title="${std.project.school.name} 学生计划完成情况"]
    bar.addPrint();
  [/@]
  <table align="center" class="infoTable">
   <tr>
    <td class="title" width="10%">学号:</td>
    <td class="content" width="20%">${std.code}</td>
    <td class="title" width="10%">姓名:</td>
    <td class="content" width="25%">${std.name}</td>
    <td class="title" width="10%">年级:</td>
    <td class="content" width="25%">${std.state.grade!}</td>
   </tr>
   <tr>
    <td class="title">院系:</td>
    <td class="content">${std.state.department.name}</td>
    <td class="title">专业/方向:</td>
    <td class="content">${std.state.major.name}&nbsp;${(std.state.direction.name)!}</td>
    <td class="title">班级:</td>
    <td class="content">${(std.state.squad.name)!}</td>
   </tr>
   <tr>
    <td class="title">层次/类别:</td>
    <td class="content">${std.level.name} / ${std.stdType.name}</td>
    <td class="title">要求/实修:</td>
    <td class="content">${result.requiredCredits}&nbsp;/&nbsp;${result.passedCredits}</td>
    <td class="title">更新时间:</td>
    <td class="content">${(result.updatedAt?string('yyyy-MM-dd HH:mm:ss'))!}
   </tr>
   [#if result.owedCredits>0]
   <tr>
     <td class="title">学分缺口:</td>
     <td><span style="color:red">${result.owedCredits}分</span>[#if result.owedCredits!=result.owedCredits3](在读课程通过后，[#if result.owedCredits3>0]<span style="color:red">缺${result.owedCredits3}分</span>[#else]可完成计划[/#if])[/#if]</td>
     <td class="title">预计说明:</td>
     <td colspan="3" class="text-muted">表示如果毕业论文等成绩通过后，计划审核的预计结果</td>
   </tr>
   [/#if]
  </table>
<div class="grid">
<table width="100%" class="grid-table">
  <thead class="grid-head">
    <tr>
      <th width="5%">课程</th>
      <th width="10%">代码</th>
      <th width="30%">名称</th>
      <th>学分</th>
      <th>完成学分</th>
      <th>成绩</th>
      <th>完成否</th>
      <th width="25%">备注</th>
    </tr>
  </thead>
  [@groupsData result.topGroupResults?sort_by('indexno'),1/]
</table>
</div>
<br><br>
[#macro groupsData courseGroups,lastNum]
  [#list courseGroups as group]
    [#if group.courseResults?size==0 && group.requiredCredits==0 && group.passed && (!group.parent?? || group.parent.passed)]
      [#--//如果上级组通过，忽略0分的组--]
      [#continue/]
    [/#if]
    <tr class="darkColumn" style="font-weight: bold">
      <td colspan="3" style="padding-left: 5px;text-align: left;">
        [#list 1..lastNum as d][/#list]${sg.next(lastNum?int)}&nbsp;${(group.name)?if_exists}
        [#if group.parent?? && !group.passed]
        <span style="color:#f1948a;font-weight: normal;">
        [#assign displayed=false/]
        [#if group.owedCredits>0](缺${group.owedCredits}分)[#assign displayed=true/][/#if]
        [#if group.neededGroups>0](要求${group.subCount}组 缺${group.neededGroups}组)[#assign displayed=true/][/#if]
        [#if !displayed]必修课未完成[/#if]
        </span>
        [/#if]
      </td>
      <td align="center">${(group.requiredCredits)?default('')}</td>
      <td align="center">${(group.passedCredits)?default('')} [#if ((group.convertedCredits)>0)](转换${(group.convertedCredits)}分)[/#if]</td>
      <td></td>
      <td align="center">
        [#if group.passed]是[#elseif !group.parent??]
          <span style="[#if group.predicted]color:#f1948a;[#else]color:red;[/#if][#if group.parent??]font-weight: normal;[/#if]">
          缺${group.owedCredits}分
          </span>
        [/#if]
      </td>
      <td align="center">&nbsp;
        [#if !group.passed && !group.parent??]
          <span class="text-muted" style="font-weight: normal;">
            [#if group.predicted]预计可通过[#else] [#if group.owedCredits3>0]预计缺${group.owedCredits3}分[/#if][/#if]
          </span>
        [/#if]
      </td>
    </tr>

     [#list group.courseResults?sort_by(["course","code"]) as courseResult]
     [#local coursePassed=courseResult.passed/]
     <tr>
         <td align="center">${courseResult_index+1}</td>
         <td width="10%" style="padding-left: 5px;text-align: left;">${(courseResult.course.code)?default('')}</td>
         <td style="padding-left: 5px;text-align: left;">${(courseResult.course.name)?default('')}</td>
         <td align="center">${(courseResult.course.getCredits(std.level))?default('')}</td>
         <td align="center">[#if coursePassed]${(courseResult.course.getCredits(std.level))?default('')}[#else]0[/#if]</td>
         <td align="center">${courseResult.scores!}</td>
         <td align="center">
         [#if !courseResult.passed]
           [#if courseResult.scores!='--']
             [#if group.passed]<span class="text-muted">否</span>
             [#elseif courseResult.taking]<span style="color:#f1948a;">否</span>
             [#else]<span style="color:red">否</span>
             [/#if]
           [#elseif courseResult.compulsory]
             <span style="color:[#if group.predicted]#f1948a;[#else]red[/#if]">否<sup>必</sup></span>
           [/#if]
         [/#if]
         </td>
         <td align="center">${courseResult.remark?if_exists}</td>
     </tr>
     [/#list]
      [#if (group.children?size!=0)]
      ${sg.reset((lastNum+1)?int)}
      [@groupsData group.children?sort_by('indexno'),lastNum+1/]
    [/#if]
  [/#list]
[/#macro]
</div>
[@b.foot/]
