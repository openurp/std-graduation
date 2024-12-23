[#ftl]
[@b.head/]
[#include "nav.ftl"/]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="searchForm" action="!search" title="ui.searchForm" target="listFrame" theme="search"]
      [@b.select name="batch.id" items=batches label="毕业年月" required="true"/]
      [@b.textfield name="batchNo" label="批次" placeholder="1,2,3" /]
     [/@]
  </div>
  <div class="search-list">
     [@b.div id="listFrame"/]
  </div>
</div>
 <script>
  var form = document.searchForm;
  function search(pageNo,pageSize,orderBy){
    form.target="listFrame";
    form.action="${b.url('!search')}";
    bg.form.submit(form)
  }
  search();
 </script>
[@b.foot/]
