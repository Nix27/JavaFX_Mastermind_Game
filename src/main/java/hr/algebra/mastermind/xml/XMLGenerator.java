package hr.algebra.mastermind.xml;

import hr.algebra.mastermind.enums.MoveType;
import hr.algebra.mastermind.model.GameMove;
import hr.algebra.mastermind.repository.SimpleGameMoveRepository;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLGenerator implements SimpleGameMoveRepository {
    public final String FILENAME = "xml/gameMoves.xml";

    @Override
    public void saveNewGameMove(GameMove newGameMove) {
        try {
            List<GameMove> allGameMoves = getAllGameMoves();
            allGameMoves.add(newGameMove);

            Document document = createDocument("gameMoves");

            for (int i = 0; i < allGameMoves.size(); i++) {
                Element gameMove = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMove);

                gameMove.appendChild(createElement(document, "moveType", allGameMoves.get(i).getMoveType().name()));
                if (allGameMoves.get(i).getRowIndex() > -1) {
                    gameMove.appendChild(createElement(document, "rowIndex", String.valueOf(allGameMoves.get(i).getRowIndex())));
                }
                gameMove.appendChild(createElement(document, "circleIndex", String.valueOf(allGameMoves.get(i).getCircleIndex())));
                gameMove.appendChild(createElement(document, "color", allGameMoves.get(i).getColor()));
            }

            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<GameMove> getAllGameMoves() {
        List<GameMove> gameMoves = new ArrayList<>();

        File xmlFile = new File(FILENAME);

        if(xmlFile.exists()){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(xmlFile);
                Element gameMovesDocumentElement = document.getDocumentElement();

                NodeList childNodesFromGameMoves = gameMovesDocumentElement.getChildNodes();

                MoveType moveType = MoveType.CODE;
                int rowIndex = -1;
                int circleIndex = 0;
                String color = "";

                for (int i = 0; i < childNodesFromGameMoves.getLength(); i++) {
                    Node gameMoveNode = childNodesFromGameMoves.item(i);

                    if (gameMoveNode.getNodeType() != Node.ELEMENT_NODE) continue;

                    Element gameMoveElement = (Element) gameMoveNode;

                    NodeList childNodesFromGameMove = gameMoveElement.getChildNodes();

                    for (int j = 0; j < childNodesFromGameMove.getLength(); j++) {
                        if (childNodesFromGameMove.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element childFromGameMove = (Element) childNodesFromGameMove.item(j);

                            switch (childFromGameMove.getTagName()) {
                                case "moveType" -> moveType = MoveType.valueOf(childFromGameMove.getTextContent());
                                case "rowIndex" -> rowIndex = Integer.parseInt(childFromGameMove.getTextContent());
                                case "circleIndex" -> circleIndex = Integer.parseInt(childFromGameMove.getTextContent());
                                case "color" -> color = childFromGameMove.getTextContent();
                            }
                        }
                    }

                    GameMove gameMove = new GameMove(moveType, circleIndex, color);
                    if (rowIndex > -1) gameMove.setRowIndex(rowIndex);

                    gameMoves.add(gameMove);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        return gameMoves;
    }

    private void saveDocument(Document document, String filename) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(filename)));
    }

    private Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, element, null);
    }

    private Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }
}
