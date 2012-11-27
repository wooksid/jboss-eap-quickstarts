/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.spring.controlleradvice.controller;

import org.jboss.as.quickstarts.kitchensink.spring.controlleradvice.data.MemberDao;
import org.jboss.as.quickstarts.kitchensink.spring.controlleradvice.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping(value = "/")
public class MemberController {
    @Autowired
    private MemberDao memberDao;

    /**
     * We move the "members" modelAttribute to {@link MemberControllerAdvice#getMembers()}
     * because it is used multiple times in various request mappings, see below.
     * This is goes with the Don't Repeat Yourself (DRY) principle.
     */
    @RequestMapping(method = RequestMethod.GET)
    public String displaySortedMembers(Model model) {
        /*model.addAttribute("members", memberDao.findAllOrderedByName());*/
        model.addAttribute("newMember", new Member());
        return "index";
    }

    /**
     * We move the "members" modelAttribute to {@link MemberControllerAdvice#getMembers()}
     * because it is used multiple times in various views, see above.
     */
    @RequestMapping(method = RequestMethod.POST)
    public String registerNewMember(@Valid @ModelAttribute("newMember") Member newMember, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                memberDao.register(newMember);
                return "redirect:/";
            } catch (UnexpectedRollbackException e) {

                /*model.addAttribute("members", memberDao.findAllOrderedByName());*/
                model.addAttribute("error", e.getCause().getCause());
                return "index";
            }
        } else {
            return "index";
        }
    }

    @RequestMapping(value = "error/", method = RequestMethod.POST)
    public void postError() throws IOException {
        throw new IOException();
    }
}