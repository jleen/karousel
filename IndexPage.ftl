<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" type="text/css" href="${browsePrefix}}carousel.css" />

    <#if breadcrumbs??>
    <title>${thisdir} - ${galleryTitle}}</title>
    <#else>
    <title>${galleryTitle}</title>
    </#if>
</head>

<body>

<#if indexHtml??>
#{indexHtml}
<#else>
<p>
    <#list breadcrumbs as crumb>
    <b><a href="${crumb.dir}">${crumb.name}</a></b>
    &gt;&gt;
    </#list>
    <b>${finalCrumb}</b>
</p>
</#if>

<div class="directory_section">
    <#list subdirs as subdir>
    <div class="directory_entry">
        <div class="directory_inner">
            <div class="directory_innerer">
                <div>
                    <a href="${subdir.dir}"><img src="${subdir.preview}" border="2" align="middle" height="${subdir.height}" width="${subdir.width}" class="dir_thumb" /></a>
                </div>
            </div>
        </div>
        <div class="directory_name">
            <a href="${subdir.dir}">${subdir.name}</a>
        </div>
    </div>
    </#list>
</div>

<#if imgurls??>
<div class="image_section">
    <#list imgurls as imgurl>
    <div class="image_entry">
        <div>
            <div class="image_inner">
                <a href="${imgurl.pageurl}"><img src="${imgurl.thumburl}" border="2" class="thumbnail" height="${imgurl.height}" width="${imgurl.width}" /></a>
                <div class="caption"><a href="${imgurl.pageurl}">${imgurl.caption}</a></div>
            </div>
        </div>
    </div>
    </#list>
</div>
</#if>
</body>
</html>