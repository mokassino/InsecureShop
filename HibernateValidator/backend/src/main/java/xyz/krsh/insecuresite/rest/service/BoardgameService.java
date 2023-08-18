package xyz.krsh.insecuresite.rest.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.codecs.MySQLCodec.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;

@Service
public class BoardgameService {
    MySQLCodec codec = new MySQLCodec(Mode.STANDARD);

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    OrderedBoardgamesRepository orderedBoardgameRepository;

    public List<Boardgame> findByNameContaining(String queryTerm) throws ItemNotFoundException {
        List<Boardgame> queryResult = boardgameRepository.findByNameContaining(queryTerm);

        if (queryResult.isEmpty()) {
            throw new ItemNotFoundException();
        }

        /*
         * Example of canonicalization then encoding for HTML
         * 
         * for (Boardgame b : queryResult) {
         * String descr = b.getDescription();
         * String canonForm = ESAPI.encoder().canonicalize(descr, false, false);
         * descr = ESAPI.encoder().encodeForHTML(canonForm);
         * b.setDescription(descr);
         * }
         */

        for (Boardgame b : queryResult) {
            Set<ConstraintViolation<Boardgame>> myConstraintViolations = validator.validate(b);
            for (ConstraintViolation<Boardgame> c : myConstraintViolations) {
                System.out.println(c.getPropertyPath());
                System.out.println(c.getMessage());
            }
            if (myConstraintViolations.size() > 1) {
                System.out.println("Violation detected");
            } else {
                System.out.println("No violation detected");
            }
        }

        return queryResult;

    }

    public Boardgame getById(String name) {
        Boardgame boardgame = boardgameRepository.findByNameContaining(name).get(0);
        return boardgame;
    }

    public Boardgame addBoardgame(String name, float price, int quantity, String description) {
        Boardgame newBoardgame = new Boardgame(name, price, quantity, description);
        boardgameRepository.save(newBoardgame);
        return newBoardgame;

    }

    public Boardgame editBoardgame(String name, Float price, Integer quantity, String description,
            HttpServletRequest request)
            throws ItemNotFoundException {
        Boardgame boardgame;

        List<Boardgame> queryResult = this.findByNameContaining(name);
        if (queryResult.size() == 0 || queryResult.isEmpty() == true) {
            throw new IndexOutOfBoundsException();
        } else {
            boardgame = queryResult.get(0);
        }

        // Check existance of params
        boolean priceParamExists = request.getParameterMap().containsKey("price");
        boolean quantityParamExists = request.getParameterMap().containsKey("quantity");
        boolean descriptionParamExists = request.getParameterMap().containsKey("description");

        // if price exists as parameter in the HTTP request, change the price
        if (priceParamExists) {
            boardgame.setPrice(price);
        }
        if (quantityParamExists) {
            boardgame.setQuantity(quantity);
        }
        if (descriptionParamExists) {

            // Encode for SQL
            // description = ESAPI.encoder().canonicalize(description);
            // description = ESAPI.encoder().encodeForSQL(codec, description);
            boardgame.setDescription(description);
        }

        // update the boardgames with new values
        boardgameRepository.update(boardgame);

        return boardgame;
    }

    public String deleteBoardgame(String name) {
        Optional<List<OrderedBoardgames>> obQueryResult = orderedBoardgameRepository.findByBoardgameName(name);
        Optional<Boardgame> bQueryResult = boardgameRepository.findById(name);

        if (obQueryResult.isPresent() && bQueryResult.isPresent()) {
            List<OrderedBoardgames> list = obQueryResult.get();
            for (OrderedBoardgames ob : list) {
                orderedBoardgameRepository.delete(ob);
            }

            Boardgame boardgame = bQueryResult.get();
            boardgameRepository.delete(boardgame);
        }

        return "Successfully deleted " + name + " ";

    }

}
