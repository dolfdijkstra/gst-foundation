package tools.gsf.facade.assetapi.asset;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to read the current asset from the ICS context.
 *
 * <pre>
 *     class MyController extends InjectingController {
 *
 *         @CurrentAsset(attributes={"title", "body", "headline"}) TemplateAsset currentTemplateAsset;
 *
 *         protected void handleRequest() {
 *             String headline = currentAsset.asString("headline");
 *         }
 *     }
 * </pre>
 * or
 * <pre>
 *     class AnotherController extends InjectingController {
 *
 *         @CurrentAsset(attributes={"title", "body", "headline"}) ScatteredAsset currentScatteredAsset;
 *
 *         protected void handleRequest() {
 *              models.add("asset", currentScatteredAsset);
 *         }
 *     }
 * </pre>
 * or
 * <pre>
 *     class ThirdController extends InjectingController {
 *
 *         @CurrentAsset(attributes={"title", "body", "headline"}) AssetData currentAssetData;
 *
 *         protected void handleRequest() {
 *              AttributeData ad = currentAssetData.getAttribute("title");
 *              ...
 *         }
 *     }
 * </pre>
 * @author Tony Field
 * @since 2016-08-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CurrentAsset {

    String[] attributes() default {"name"};

}
