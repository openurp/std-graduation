[#ftl]
[@b.head/]
<style>
@page{
   size: landscape;
}
</style>
<div>
    [#assign gLists = res?sort_by(["std","code"])?chunk(20)]
    [#assign gIndex=1]
    [#list gLists  as gList]
    <h4 width="100%" align="center" style="margin-bottom:5px">${session.project.school.name }学历、学位证书签收名册
       <br>延长生
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
      [#list gList as g]
       <tr  align="center" style="height:26px">
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
         <td><B><br>辅导员:</B>____________________<br>[#list 1..15 as i]&nbsp;[/#list]${b.now?string('yyyy年MM月')}</td>
       </tr>
   </table>
   [#if  gList_has_next]
   <div style='PAGE-BREAK-AFTER: always'></div>
   [/#if]
   [/#list]
</div>
[@b.foot/]
