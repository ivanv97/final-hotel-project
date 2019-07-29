package eu.deltasource.internship.hotel.domain.commodity;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class Toilet extends AbstractCommodity {

    public Toilet() {
        super();
    }

    @Override
    public void prepare() {
        System.out.println("The toilet is being cleaned!");
    }

}
