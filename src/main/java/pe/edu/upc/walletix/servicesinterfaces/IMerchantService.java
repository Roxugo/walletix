package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Merchant;

import java.util.List;
import java.util.Optional;

public interface IMerchantService {
    public List<Merchant> list();
    public Merchant insert(Merchant m);
    public Optional<Merchant> listId(int id);
    public void update(Merchant m);
    public void delete(int id);
}
