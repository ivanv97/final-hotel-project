package eu.deltasource.internship.hotel.domain.commodity;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class Shower extends AbstractCommodity {

    public Shower() {
        super();
    }

    @Override
    public void prepare() {
        System.out.println("The shower is being cleaned!");
    }

}
