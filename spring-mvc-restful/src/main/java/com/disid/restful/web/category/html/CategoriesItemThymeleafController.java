package com.disid.restful.web.category.html;

import com.disid.restful.model.Category;
import com.disid.restful.service.api.CategoryService;

import io.springlets.web.NotFoundException;
import io.springlets.web.mvc.util.ControllerMethodLinkBuilderFactory;
import io.springlets.web.mvc.util.MethodLinkBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;

import java.util.Locale;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/categories/{category}", name = "CategoriesItemThymeleafController",
    produces = MediaType.TEXT_HTML_VALUE)
public class CategoriesItemThymeleafController {

  public CategoryService categoryService;
  private MessageSource messageSource;
  private MethodLinkBuilderFactory<CategoriesItemThymeleafController> itemLink;

  @Autowired
  public CategoriesItemThymeleafController(CategoryService categoryService,
      MessageSource messageSource, ControllerMethodLinkBuilderFactory linkBuilder) {
    this.categoryService = categoryService;
    this.messageSource = messageSource;
    this.itemLink = linkBuilder.of(CategoriesItemThymeleafController.class);
  }

  @InitBinder("category")
  public void initCustomerBinder(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  @ModelAttribute
  public Category getCategory(@PathVariable("category") Long id, Locale locale) {
    Category category = categoryService.findOne(id);
    if (category == null) {
      String message = messageSource.getMessage("error_categoryNotFound", null, locale);
      throw new NotFoundException(message);
    }
    return category;
  }

  @GetMapping(value = "/edit-form", name = "editForm")
  public ModelAndView editForm(@ModelAttribute Category category, Model model) {
    return new ModelAndView("categories/edit");
  }

  @PutMapping(name = "update")
  public ModelAndView update(@Valid @ModelAttribute Category category, BindingResult result,
      Model model) {
    if (result.hasErrors()) {
      return new ModelAndView("categories/edit");
    }
    Category savedCategory = categoryService.save(category);
    UriComponents showURI = itemLink.to("show").with("category", savedCategory.getId()).toUri();
    return new ModelAndView("redirect:" + showURI.toUriString());
  }

  @DeleteMapping(name = "delete")
  @ResponseBody
  public ResponseEntity<?> delete(@ModelAttribute Category category) {
    categoryService.delete(category);
    return ResponseEntity.ok().build();
  }

  @GetMapping(name = "show")
  public ModelAndView show(@ModelAttribute Category category, Model model) {
    return new ModelAndView("categories/show");
  }
}
