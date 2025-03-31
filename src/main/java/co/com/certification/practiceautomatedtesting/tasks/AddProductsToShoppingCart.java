package co.com.certification.practiceautomatedtesting.tasks;

import co.com.certification.practiceautomatedtesting.interactions.AddProduct;
import co.com.certification.practiceautomatedtesting.questions.CheckNameProduct;
import co.com.certification.practiceautomatedtesting.utils.various.EnumVariablesSesion;
import lombok.AllArgsConstructor;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static net.serenitybdd.screenplay.Tasks.instrumented;


@AllArgsConstructor
public class AddProductsToShoppingCart implements Task {
    private final int products;

    public <T extends Actor> void performAs(T actor) {
        for (int i = 1; i <= products; i++) {
            String productId = String.valueOf(i);
            actor.attemptsTo(
                    AddProduct.buyProduct(productId)
            );
            if (i == 1) {
                Serenity.setSessionVariable(EnumVariablesSesion.SESSION_PRODUCT_ONE.getValue()).to(CheckNameProduct.
                        productName(productId).answeredBy(actor));
            } else {
                Serenity.setSessionVariable(EnumVariablesSesion.SESSION_PRODUCT_TWO.getValue()).to(CheckNameProduct.
                        productName(productId).answeredBy(actor));
            }
        }
    }

    public static AddProductsToShoppingCart addProductCart(int products) {
        return instrumented(AddProductsToShoppingCart.class, products);
    }

}
