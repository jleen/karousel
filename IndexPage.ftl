<#-- @ftlvariable name="" type="IndexPageModel" -->
<!DOCTYPE html>
<html lang="en-US">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" type="text/css" href="${browsePrefix}/carousel.css" />

    <#if breadcrumbs??>
    <title>${thisDir} - ${galleryTitle}</title>
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
    <#list subDirs as subDir>
    <div class="directory_entry">
        <div class="directory_inner">
            <div class="directory_innerer">
                <div>
                    <a href="${subDir.dir}"><img src="${subDir.preview}" border="2" align="middle" height="${subDir.height}" width="${subDir.width}" class="dir_thumb" /></a>
                </div>
            </div>
        </div>
        <div class="directory_name">
            <a href="${subDir.dir}">${subDir.name}</a>
        </div>
    </div>
    </#list>
</div>

<#if imgUrls??>
<div class="image_section">
    <#list imgUrls as imgUrl>
    <div class="image_entry">
        <div>
            <div class="image_inner">
                <a href="${imgUrl.pageUrl}"><img src="${imgUrl.thumbUrl}" border="2" class="thumbnail" height="${imgUrl.height}" width="${imgUrl.width}" /></a>
                <div class="caption"><a href="${imgUrl.pageUrl}">${imgUrl.caption}</a></div>
            </div>
        </div>
    </div>
    </#list>
</div>
</#if>
</body>
</html>