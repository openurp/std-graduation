[#ftl]
[@b.head/]
<style>
@page{
   size: landscape;
}
</style>
<div class="container">
  [#list squads as squad]
    [#assign gLists = res[squad.id?string]?sort_by(["std","code"])?chunk(20)]
    [#assign gIndex=1]
    [#list gLists  as gList]
    <h4 width="100%" align="center" style="margin-bottom:5px;font-size: 1rem;font-weight: bold;">${squad.project.school.name }学历、学位证书签收名册
       <br>${squad.department.name} ${squad.name}
    </h4>
    <table class="grid-table" align="center" style="border:1px">
     <thead class="grid-head">
       <tr align="center">
         <th width="4%">序号</th>
         <th width="10%">学号</th>
         <th width="6%">姓名</th>
         <th width="4%">性别</th>
         <th width="7%">出生日期</th>
         <th width="21%">毕业证书号</th>
         <th width="21%">学位证书号</th>
         <th width="9%">电话</th>
         <th width="9%">签收</th>
         <th width="9%">日期</th>
       </tr>
     </thead>
     <tbody>
      [#list gList as g]
       <tr align="center" style="height:26px">
         <td>${gIndex}[#assign gIndex=gIndex+1]</td>
         <td>${g.std.code}</td>
         <td>${g.std.name}</td>
         <td>${g.std.person.gender.name}</td>
         <td>${(g.std.person.birthday?string('yyyyMMdd'))!}</td>
         <td>${(g.certificateNo)!'--'}</td>
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
       <td><B><br>
       [#assign masterName=""/]
       [#if squad.master??]班主任:[#assign masterName=squad.master.name/][#else]辅导员:[#assign masterName=(squad.mentor.name)!""/][/#if]</B>
        <u>[#list 1..8 as i]&nbsp;[/#list]${masterName}[#list 1..8 as i]&nbsp;[/#list]</u><br>[#list 1..15 as i]&nbsp;[/#list]${batch.graduateOn?string('yyyy年MM月')}
       </td>
     </tr>
   </table>
   [#if  gList_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
   [/#list]
   [#if  squad_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
  [/#list]
</div>
[@b.foot/]
