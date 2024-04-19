[#ftl/]
[@b.head/]
[@b.toolbar title="毕业审核"/]

<div class="search-container">
  <div class="search-panel">
    [@b.form theme="search" name="searchForm" action="!search" title="查询条件" target="contentDiv" ]
      [#include "searchForm.ftl"/]
    [/@]
  </div>
  <div class="search-list">
     [@b.div id="contentDiv" /]
  </div>
</div>
<script>
  jQuery(function() {
    bg.form.submit(document.searchForm);
  });
</script>
[@b.foot/]
