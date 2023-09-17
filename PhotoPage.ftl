<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${browsePrefix}\carousel.css">
    <title>${pageTitle} - ${galleryTitle}</title>
</head>

<body>
<#if breadcrumbs??>
<p>
    <#list breadcrumbs as crumb>
    <b><a href="${crumb.dir}">${crumb.name}</a></b>
    &gt;&gt;
    </#list>
    <b>${finalCrumb}</b>
</p>
</#if>

<div class="container">
    <div class="side_left">
        <#if prev??>
        <a href="${prev}">&lt;&lt; Previous</a>
        </#if>
    </div>

    <div>
        <div align="center">
            <div class="top">
                <div class="top_left">
                    <#if prev??>
                    <a href="${prev}">&lt;&lt; Previous</a>
                    </#if>
                </div>
                <div class="top_right">
                    <#if next??>
                    <a href="${next}">Next &gt;&gt;</a>
                    </#if>
                </div>
            </div>

            <a href="${fullPhotoUrl}"><img class="itself" src="${framedPhotoUrl}" height="${height}" width="${width}" border="2" vspace="10" align="middle"></a>
            <div class="phototitle"><b>${caption}</b></div><br>
        </div>
    </div>

    <div class="side_right">
        <#if next??>
        <a href="${next}">Next &gt;&gt;</a>
        </#if>
    </div>
</div>
</body>
</html>