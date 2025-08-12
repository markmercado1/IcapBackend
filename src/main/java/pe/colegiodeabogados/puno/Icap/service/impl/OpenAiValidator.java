
package pe.colegiodeabogados.puno.Icap.service.impl;


import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiValidator {

    private final OpenAiService openAiService;

    public OpenAiValidator(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String validarDocumento(String texto) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt("Analiza el siguiente texto y responde si parece ser un certificado válido que incluye DNI y firma. Solo responde con una validación breve. Texto:\n\n" + texto)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(150)
                .temperature(0.2)
                .build();

        return openAiService.createCompletion(request)
                .getChoices()
                .get(0)
                .getText()
                .trim();
    }
}
