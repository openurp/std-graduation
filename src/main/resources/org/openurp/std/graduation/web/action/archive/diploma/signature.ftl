[#ftl]
[@b.head/]
<style>
@page {
  size: a4 landscape;
}
</style>
<div class="container">
  [#list squads?sort_by('name') as squad]
    [#assign arLists = res[squad.id?string]?sort_by(["std","code"])?chunk(20)]
    [#assign gIndex=1]
    [#list arLists  as arList]
    <h3 width="100%" align="center" style="margin-bottom:5px;font-size: 1rem;font-weight: bold;">${squad.project.school.name }学位证书签收表<br>${squad.name}</h3>

    <table class="grid-table"  width="100%"  align="center" style="border:0.5px solid #006CB2">
     <thead class="grid-head">
       <tr>
         <th width="4%">序号</th>
         <th width="10%">学号</th>
         <th width="6%">姓名</th>
         <th width="4%">性别</th>
         <th width="7%">出生日期</th>
         <th width="16%">身份证号</th>
         <th width="26%">学位证书号</th>
         <th width="9%">电话</th>
         <th width="9%">签收</th>
         <th width="9%">日期</th>
       </tr>
     </thead>
     <tbody>
      [#list arList as g]
       <tr align="center" style="height:26px">
         <td>${gIndex}[#assign gIndex=gIndex+1]</td>
         <td>${g.std.code}</td>
         <td>${g.std.name}</td>
         <td>${g.std.gender.name}</td>
         <td>${(g.std.person.birthday?string('yyyyMMdd'))!}</td>
         <td>${(g.std.person.code)!}</td>
         <td>${(g.diplomaNo)!'--'}</td>
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
         <td><B><br>班主任:</B><U>[#list 1..8 as i]&nbsp;[/#list]${(squad.master.name)!}[#list 1..8 as i]&nbsp;[/#list]</U><br>[#list 1..15 as i]&nbsp;[/#list]${batch.graduateOn?string('yyyy年MM月')}</td>
       </tr>
   </table>
   [#if  arList_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
   [/#list]
   [#if  squad_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
  [/#list]
</div>
[@b.foot/]
