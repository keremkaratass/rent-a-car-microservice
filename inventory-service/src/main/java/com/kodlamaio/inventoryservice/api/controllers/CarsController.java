package com.kodlamaio.inventoryservice.api.controllers;

import com.kodlamaio.inventoryservice.business.abstracts.CarService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.all.GetAllCarsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateCarResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cars")
public class CarsController {
    private final CarService service;

    @GetMapping
    // Secured, PreAuthorize, PostAuthorize, PosFilter, PreFilter (Son ikisi koleksiyana uygulanıyor)
    //Secured ve diğer ikisinin farkı ise secured daha basit kurallar için kullanılıyor.
    //@Secured("ROLE_admin") ROLE(prefixi) olmalı çünkü security bunu bekliyor. burada ve veya kullanamıyoruz. basşt tanımalamalar için.
    @PreAuthorize("hasRole('user') and hasRole('admin')") //SpeL kullanabilriz.
    public List<GetAllCarsResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('admin') || returnObject.modelYear ==2019")
    //@PostAuthorize("hasRole('admin') || returnObject.modelYear == #jwt.subject")  meselea payment düşün kendi ödeme bilgilerimi görüntüleyebilmeliyim
    // payment modeline bir tane customer ıd ekliyorum. İşte sonra diyorum ki kullanıcı ödemelere
    //getbyıd yaptığında ya admin olmalı ya da süperadmin olmalı veya kendi idsi yani customer idsi tokendaki suba eşitse artık okeydir diyebiliriz.
    //returnObject.customerıd == #jwt.subject mesela buradaki gibi aynı. bu aşağıdaki sub kısmına eşit. SpeL tarafında böyle kullanılıyor.
    // buradada kıyaslama yapabiliyoruz. bize buna olanak sağlıyor. yani aslında bunun olayı modelin içerisindeki verielre erişebilmemiz.
    public GetCarResponse getById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println(jwt.getClaims().get("email"));
        System.out.println(jwt.getClaims().get("sub"));
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCarResponse add(@Valid @RequestBody CreateCarRequest request) {
        return service.add(request);
    }

    @PutMapping("/{id}")
    public UpdateCarResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCarRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/check-car-available/{id}")
    public void checkIfCarAvailable(@PathVariable UUID id) {
        service.checkIfCarAvailable(id);
    }
}
