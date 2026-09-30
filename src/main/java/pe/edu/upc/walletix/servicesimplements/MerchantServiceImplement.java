package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Merchant;
import pe.edu.upc.walletix.repositories.IMerchantRepository;
import pe.edu.upc.walletix.servicesinterfaces.IMerchantService;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantServiceImplement implements IMerchantService {

    @Autowired
    private IMerchantRepository mR;

    @Override
    public List<Merchant> list() {
        return mR.findAll();
    }

    @Override
    public Merchant insert(Merchant m) {
        return mR.save(m);
    }

    @Override
    public Optional<Merchant> listId(int id) {
        return mR.findById(id);
    }

    @Override
    public void update(Merchant m) {
        mR.save(m);
    }

    @Override
    public void delete(int id) {
        mR.deleteById(id);
    }
}
