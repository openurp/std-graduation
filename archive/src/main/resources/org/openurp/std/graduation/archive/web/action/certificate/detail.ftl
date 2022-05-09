[#ftl]
[@b.head/]
<style>
@page{
   size: portrait;
}
</style>
<div style="font-size:13px">
  [#list squads as adc]
    <h4 width="100%" align="center" style="margin-bottom:5px">${adc.project.school.name }学历、学位证书名册
       <br>${adc.department.name} ${adc.name}
    </h4>
    <table class="gridtable" align="center" style="border:1px">
     <thead class="gridhead">
       <tr align="center">
         <td width="6%">序号</td>
         <td width="15%">学号</td>
         <td width="10%">姓名</td>
         <td width="8%">性别</td>
         <td width="11%">出生日期</td>
         <td width="25%">毕业证书号</td>
         <td width="25%">学位证书号</td>
       </tr>
     </thead>
     <tbody>
      [#list res[adc.id?string]?sort_by(["std","user","code"]) as g]
       <tr  align="center">
         <td>${g_index+1}</td>
         <td>${g.std.user.code}</td>
         <td>${g.std.user.name}</td>
         <td>${g.std.person.gender.name}</td>
         <td>${(g.std.person.birthday?string('yyyyMMdd'))!}</td>
         <td>${(g.certificateNo)!'--'}</td>
         <td>${(g.diplomaNo)!'--'}</td>
       </tr>
       [/#list]
      </tbody>
   </table>
   <table width="100%" align="center">
       <tr>
         <td width="6%"></td>
         <td><B><br>辅导员:</B>______________________________<br>[#list 1..15 as i]&nbsp;[/#list] _______年______月_______日</td>
       </tr>
   </table>
   [#if  adc_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
  [/#list]
</div>
[@b.foot/]
