
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
