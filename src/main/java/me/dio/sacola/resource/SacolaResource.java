package me.dio.sacola.resource;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.resource.dto.ItemDto;
import me.dio.sacola.service.SacolaService;
import org.springframework.web.bind.annotation.*;

@Api(value = "/ifood-devweek/sacolas")

@RequiredArgsConstructor
@RestController
@RequestMapping("/ifood-devweek/sacolas")
public class SacolaResource {
    private final SacolaService sacolaServices;

    @PostMapping
    public Item incluirItemNaSacola(@RequestBody ItemDto itemDto){
        return sacolaServices.incluirItemNaSacola(itemDto);
    }

    @GetMapping("/{id}")
    public Sacola verSacola(@PathVariable("id") Long id){
        return sacolaServices.verSacola(id);
    }

    @PatchMapping("/fecharSacola/{idSacola}")
    public Sacola fecharSacola(@PathVariable("idSacola") Long idSacola, @RequestParam("formaPagamento") int formaPagamento){
        return sacolaServices.fecharSacola(idSacola, formaPagamento);
    }
}
