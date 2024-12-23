[#ftl]
[@b.head/]
<style>
@page {
  size: a4 landscape;
}
</style>
<div class="container">
   <H4 width="95%" align="center" style="margin-bottom:5px">${graduateBatch.graduateOn?string('YYYY')}年${graduateBatch.graduateOn?string('MM')}月学位证书签收表</H4>

   <table class="grid-table" width="95%" align="center">
     <thead  class="grid-head">
       <tr style="height:26px;">
         <th width="4%">序号</th>
         <th width="13%">院系</th>
         <th width="37%">班级</th>
         <th width="6%">人数</th>
         <th width="10%">签收</th>
         <th width="15%">日期</th>
         <th width="10%">班主任</th>
         <th width="5%">备注</th>
       </tr>
     </thead>
     <tbody class="grid-body">
     [#assign lastDepart=""/]
     [#assign totalInDepart=0/]
     [#list squads as squad]
       [#if lastDepart?length ==0 ]
        [#assign lastDepart=squad.department.name/]
       [/#if]
       [#if squad.department.name != lastDepart]
       <tr style="height:26px">
         <td colspan="6"></td>
         <td colspan="2" style="text-align:center">小计:${totalInDepart}人</td>
         [#assign totalInDepart=0/]
         [#assign lastDepart=squad.department.name/]
       </tr>
       [/#if]
       <tr style="height:26px">
         <td>${squad_index+1}</td>
         <td>${squad.department.name}</td>
         <td style="text-align:left;padding-left: 5px;">${squad.name}</td>
         <td>${squadMap.get(squad)} [#assign totalInDepart=totalInDepart+squadMap.get(squad)]</td>
         <td></td>
         <td></td>
         <td>${(squad.master.name)!}</td>
         <td></td>
       </tr>

       [/#list]
       [#if lastDepart?length !=0]
       <tr style="height:26px">
         <td colspan="6"></td>
         <td colspan="2">小计:${totalInDepart}人</td>
       </tr>
       [/#if]
     </tbody>
   </table>
</div>
[@b.foot/]
