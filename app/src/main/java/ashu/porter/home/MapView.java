package ashu.porter.home;


public interface MapView {
    void fillSource(String address);
    void populateEstimate(double cost, int time);
    void fillDestination(String address);
    void showBlocked();
    void showUnblocked();

}
