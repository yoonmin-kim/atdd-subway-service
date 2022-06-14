package nextstep.subway.favorite.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.favorite.application.FavoriteService;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity save(@AuthenticationPrincipal LoginMember loginMember,
                               @RequestBody FavoriteRequest favoriteRequest) {
        final FavoriteResponse favoriteResponse = favoriteService.saveFavoriteOfMine(loginMember.getId(), favoriteRequest);
        return ResponseEntity
                .created(URI.create("/favorites/" + favoriteResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> search(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity
                .ok(favoriteService.findFavoriteOfMine(loginMember.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@AuthenticationPrincipal LoginMember loginMember,
                                 @PathVariable("id") Long id) {
        favoriteService.deleteFavoriteOfMine(loginMember.getId(), id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
