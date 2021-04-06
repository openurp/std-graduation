[#ftl]
[@b.head/]
<div class="container">
  [#list squads as adc]
    [#assign arLists = res[adc.id?string]?sort_by(["std","user","code"])?chunk(20)]
    [#assign arIndex=1]
    [#list arLists  as arList]
    <h4 width="100%" align="center" style="margin-bottom:5px">${adc.project.school.name }毕业文凭签收名册
       <br>${adc.department.name} ${adc.name}
    </h4>
    <table class="gridtable" align="center" style="border:1px">
     <thead class="gridhead">
       <tr align="center">
         <td width="4%">序号</td>
         <td width="10%">学号</td>
         <td width="6%">姓名</td>
         <td width="4%">性别</td>
         <td width="7%">出生日期</td>
         <td width="21%">毕业证书号</td>
         <td width="21%">学位证书号</td>
         <td width="9%">电话</td>
         <td width="9%">签收</td>
         <td width="9%">日期</td>
       </tr>
     </thead>
     <tbody>
      [#list arList as ar]
       <tr  align="center" style="height:26px">
         <td>${arIndex}[#assign arIndex=arIndex+1]</td>
         <td>${ar.std.user.code}</td>
         <td>${ar.std.user.name}</td>
         <td>${ar.std.person.gender.name}</td>
         <td>${(ar.std.person.birthday?string('yyyyMMdd'))!}</td>
         <td>${(graduationMap.get(ar.std).code)!'--'}</td>
         <td>${(graduationMap.get(ar.std).diplomaNo)!'--'}</td>
         <td></td>
         <td></td>
         <td></td>
       </tr>
       [/#list]
      </tbody>
   </table>
   <table width="100%" align="center">
       <tr>
         <td width="64%"></td>
         <td><B><br>辅导员:</B>____________________<br>[#list 1..15 as i]&nbsp;[/#list]${b.now?string('yyyy年MM月')}</td>
       </tr>
   </table>
   [#if  arList_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
   [/#list]
   [#if  adc_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
  [/#list]
</div>
[@b.foot/]
